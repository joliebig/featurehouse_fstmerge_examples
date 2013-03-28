using System;
using System.Collections.Generic;
using System.Text;
using System.IO.Ports;
namespace ThoughtWorks.CruiseControl.CCTrayLib.X10
{
    public partial class Cm11LowLevelDriver
    {
        private class Cm11LowLevelDriverWorker
        {
            public event System.EventHandler<Cm11LowLevelDriverError> Error;
            private System.Collections.Generic.Queue<Cm11Message> queue = new System.Collections.Generic.Queue<Cm11Message>();
            private object queuePadLock = new object();
            private volatile bool stopWorker = false;
            private SerialPort comm;
            private CM11aHouseCode x10HouseCode;
            private int transmissionRetries = 5;
            static readonly byte[] okBuffer = new byte[1] { 0x00 };
            static readonly int comRetryInterval = 5000;
            private bool supressComErrors = false;
            private DateTime lastComTry = DateTime.Now.AddSeconds(-10);
            private string comPort;
            private int baudRate;
            private Parity parity;
            private int dataBits;
            private StopBits stopBits;
            public Cm11LowLevelDriverWorker(CM11aHouseCode houseCode, string comPort, int baudRate, Parity parity, int dataBits,
                                  StopBits stopBits)
            {
                x10HouseCode = houseCode;
                this.comPort = comPort;
                this.baudRate = baudRate;
                this.parity = parity;
                this.dataBits = dataBits;
                this.stopBits = stopBits;
            }
            public void StartProcessing()
            {
                while (!stopWorker)
                {
                    ProcessCommand();
                    System.Threading.Thread.Sleep(500);
                }
                this.CloseDriver();
                return;
            }
            public void CloseDriver()
            {
                if (comm != null)
                {
                    if (comm.IsOpen)
                        try
                        {
                            comm.Close();
                        }
                        catch (System.IO.IOException)
                        {
                        }
                    comm = null;
                }
            }
            public void AddMessage(Cm11Message message)
            {
                lock (queuePadLock)
                {
                    if (this.queue.Count < 100)
                    {
                        this.queue.Enqueue(message);
                    }
                    else
                    {
                        RaiseErrorEvent(new Cm11LowLevelDriverError("Communication to CM11 interface is probably down and message queue is full."));
                    }
                }
            }
            public void StopProcessing()
            {
                stopWorker = true;
            }
            public int TransmissionRetries
            {
                get { return (transmissionRetries); }
                set { transmissionRetries = value; }
            }
            private void ProcessCommand()
            {
                if (queue.Count > 0)
                {
                    Cm11Message command = queue.Peek();
                    if (command != null)
                    {
                        try
                        {
                            if (Send(command.Buffer, command.Count))
                            {
                                if (SendOk())
                                {
                                    lock (queuePadLock)
                                    {
                                        queue.Dequeue();
                                    }
                                }
                            }
                        }
                        catch (System.TimeoutException)
                        {
                            RaiseErrorEvent(new Cm11LowLevelDriverError("Sending command to CM11 interace failed. Is the CM11 interface still plugged in to the AC outlet?"));
                        }
                    }
                }
            }
            private bool ComPortCreated()
            {
                if (comm == null && DateTime.Now.Subtract(lastComTry).TotalMilliseconds> comRetryInterval)
                {
                    lastComTry = DateTime.Now;
                    try
                    {
                        comm = new SerialPort(comPort, baudRate, parity, dataBits, stopBits);
                        supressComErrors = false;
                        lastComTry = DateTime.Now.AddSeconds(-10);
                    }
                    catch (System.UnauthorizedAccessException)
                    {
                        if(!supressComErrors)
                            RaiseErrorEvent(new Cm11LowLevelDriverError("Access error. " + comPort + " is probably already in use by another application. Will not send this message again."));
                        supressComErrors = true;
                        return false;
                    }
                    catch (Exception ex)
                    {
                        if(!supressComErrors)
                            RaiseErrorEvent(new Cm11LowLevelDriverError("An error occurred trying to initiate communications through " + comPort + " Message: " + ex.Message + " Will not send this message again"));
                        supressComErrors = true;
                        return false;
                    }
                }
                return true;
            }
            private bool CommsOpen()
            {
                if (!ComPortCreated())
                    return false;
                if (!comm.IsOpen && DateTime.Now.Subtract(lastComTry).TotalMilliseconds > comRetryInterval)
                {
                    lastComTry = DateTime.Now;
                    try
                    {
                        comm.ReadTimeout = 3000;
                        comm.Open();
                        supressComErrors = false;
                    }
                    catch (System.IO.IOException ioEx)
                    {
                        if(!supressComErrors)
                            RaiseErrorEvent(new Cm11LowLevelDriverError("Could not open communications: " + ioEx.Message));
                        supressComErrors = true;
                    }
                    catch (System.UnauthorizedAccessException)
                    {
                        if (!supressComErrors)
                            RaiseErrorEvent(new Cm11LowLevelDriverError("Access error. " + comPort + " is probably already in use by another application."));
                        supressComErrors = true;
                    }
                    catch (System.TimeoutException)
                    {
                        if (!supressComErrors)
                            RaiseErrorEvent(new Cm11LowLevelDriverError("Time out trying to open " + comPort));
                        supressComErrors = true;
                    }
                }
                return comm.IsOpen;
            }
            private bool Send(byte[] buffer, int count)
            {
                if (!CommsOpen())
                {
                    return false;
                }
                int retVal = 0;
                int x = 0;
                do
                {
                    comm.DiscardInBuffer();
                    comm.Write(buffer, 0, count);
                    retVal = comm.ReadByte();
                    if (0x5a == retVal)
                    {
                        byte[] sendpollack = new byte[1];
                        sendpollack[0] = 0xc3;
                        comm.Write(sendpollack, 0, 1);
                    }
                    if (0xa5 == retVal)
                    {
                        byte[] timerDownload = TimerDownloadMessage(DateTime.Now);
                        comm.Write(timerDownload, 0, timerDownload.Length);
                        int ignoreNum = comm.ReadByte();
                        comm.Write(okBuffer, 0, 1);
                    }
                    x++;
                } while ((Checksum(buffer, count) != retVal) && this.TransmissionRetries > x);
                if (this.TransmissionRetries <= x)
                {
                    RaiseErrorEvent(new Cm11LowLevelDriverError("Failed to communicate with X10 controller device"));
                    return false;
                }
                return true;
            }
            private bool SendOk()
            {
                return Send(okBuffer , 1);
            }
            private byte[] TimerDownloadMessage(DateTime date)
            {
                int minute = date.Minute;
                int hour = date.Hour / 2;
                if (Math.IEEERemainder(date.Hour, 2) > 0)
                {
                    minute += 60;
                }
                int wday = Convert.ToInt16(Math.Pow(2, (int)date.DayOfWeek));
                int yearDay = date.DayOfYear - 1;
                if (yearDay > 255)
                {
                    yearDay = yearDay - 256;
                    wday = wday + Convert.ToInt16(Math.Pow(2, 7));
                }
                byte[] message = new byte[7];
                message[0] = 0x9b;
                message[1] = Convert.ToByte(date.Second);
                message[2] = Convert.ToByte(minute);
                message[3] = Convert.ToByte(hour);
                message[4] = Convert.ToByte(yearDay);
                message[5] = Convert.ToByte(wday);
                message[6] = Convert.ToByte(0x03 + (int)this.x10HouseCode);
                return message;
            }
            private int Checksum(byte[] buffer, int count)
            {
                if (1 < count)
                {
                    byte iRetVal = 0;
                    foreach (byte element in buffer)
                    {
                        iRetVal += element;
                    }
                    return (iRetVal & (byte)0xFF);
                }
                else if (1 == count)
                {
                    if (0 == buffer[0])
                    {
                        return (0x55);
                    }
                }
                return (0x00);
            }
            private void RaiseErrorEvent(Cm11LowLevelDriverError error)
            {
                if (this.Error != null)
                    this.Error(this, error);
            }
        }
    }
}
