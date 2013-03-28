import genj.gedcom.Fam;
import genj.gedcom.Gedcom;
import genj.gedcom.Indi;
import genj.gedcom.PropertyDate;
import genj.gedcom.TagPath;
import genj.gedcom.time.Delta;
import genj.gedcom.time.PointInTime;
import genj.report.Report;
import genj.util.swing.Action2;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;



public class ReportCalendar extends Report
{
    private static final TagPath PATH_INDIDEATPLAC = new TagPath("INDI:DEAT:PLAC");

    
    public boolean output_births = true;

    
    public boolean output_deaths = true;

    
    public int assume_dead = 100;

    
    public boolean dead_birthdays = false;

    
    public int anniversary = 0;
    public String[] anniversarys = { translate("both_alive"), translate("one_alive"), translate("all"), translate("none") };

    
    public int year_mode = 0;
    public String[] year_modes = { translate("upcoming"), translate("generic") };

    
    public int max_names = 0;
    public String[] max_namess = { translate("nolimit"), "1", "2", "3" };

    private static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");
    private Writer writer;

    
    public void start(Gedcom gedcom) throws IOException {

        File file = getFileFromUser("Choose calendar file", Action2.TXT_OK, true, "ics");
        if (file == null)
            return;

        writer = new FileWriter(file);
        outputHeader();

        @SuppressWarnings("unchecked")
        Collection<Indi> individuals = (Collection<Indi>)gedcom.getEntities(Gedcom.INDI);
        @SuppressWarnings("unchecked")
        Collection<Fam> families = (Collection<Fam>)gedcom.getEntities(Gedcom.FAM);

        for (Indi indi : individuals)
        {
            if (output_births)
                outputBirthday(indi);
            if (output_deaths)
                outputDeathAnniv(indi);
            
        }

        for (Fam fam : families)
        {
            if (anniversary != 3)
                outputWeddingAnniv(fam);
            
        }
        outputFooter();
        writer.close();
    }

    
    private void outputHeader() throws IOException
    {
        writer.write("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:GenJ-ReportCalendar\n");
    }

    
    private void outputFooter() throws IOException
    {
        writer.write("END:VCALENDAR\n");
    }

    
    private void outputBirthday(Indi indi) throws IOException
    {
        Event event = getDate(indi.getBirthDate());
        if (event == null)
            return;

        if (!dead_birthdays && !isAlive(indi, event.date))
            return;

        String summary = translate("birthday") + ": " + getIndiNameId(indi);
        outputEvent(event, summary);
    }

    
    private void outputDeathAnniv(Indi indi) throws IOException
    {
        Event event = getDate(indi.getDeathDate());
        if (event == null)
            return;

        String summary = translate("death_anniversary") + ": " + getIndiNameId(indi);
        outputEvent(event, summary);
    }

    
    private void outputWeddingAnniv(Fam fam) throws IOException
    {
        Event event = getDate(fam.getMarriageDate());
        if (event == null)
            return;

        
        Indi wife = fam.getWife();
        Indi husband = fam.getHusband();
        boolean wifeDead = false;
        boolean husbandDead = false;

        if (wife != null)
            wifeDead = !isAlive(wife, event.date);
        if (husband != null)
            husbandDead = !isAlive(husband, event.date);

        if (anniversary == 0 && (wifeDead || husbandDead))
            return;
        if (anniversary == 1 && wifeDead && husbandDead)
            return;

        String summary = translate("wedding_anniversary") + ": " + getFamName(fam);
        outputEvent(event, summary);
    }

    
    private void outputEvent(Event event, String summary) throws IOException
    {
        if (year_mode == 0)
            summary = event.count + " " + summary;

        summary = summary.replace(",", "\\,"); 

        writer.write("BEGIN:VEVENT\n");
        writer.write("DTSTART:" + DATE_FORMAT.format(event.date) + "\n");
        writer.write("SUMMARY:" + summary + "\n");
        if (year_mode == 1)
        {
            
            writer.write("RRULE:FREQ=YEARLY\n");
        }
        writer.write("END:VEVENT\n");
    }

    
    private Event getDate(PropertyDate date)
    {
        if (date == null)
            return null;

        
        if (date.getFormat() != PropertyDate.DATE)
            return null;
        if (!date.getStart().isComplete())
            return null;

        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();

        PointInTime pit = date.getStart();

        cal.set(now.get(Calendar.YEAR), pit.getMonth(), pit.getDay() + 1);
        if (cal.before(now))
            cal.roll(Calendar.YEAR, true);

        int count = cal.get(Calendar.YEAR) - date.getStart().getYear();

        return new Event(cal.getTime(), count);
    }

    
    private String getIndiNameId(Indi indi)
    {
        return (getIndiName(indi) + " (" + indi.getId() + ")").trim();
    }

    
    private String getIndiName(Indi indi)
    {
       return (getFirstNames(indi) + " " + indi.getLastName()).trim();
    }

    
    private String getFamName(Fam fam)
    {
        Indi wife = fam.getWife();
        Indi husband = fam.getHusband();
        String id = "(" + fam.getId() + ")";

        if (wife == null && husband == null)
            return id;

        if (wife == null)
            return getIndiName(husband) + " + " + translate("wife") + " " + id;
        if (husband == null)
            return getIndiName(wife) + " + " + translate("husband") + " " + id;

        return getFirstNames(wife) + " + " + getIndiName(husband) + " " + id;
    }

    
    private String getFirstNames(Indi indi)
    {
        String firstName = indi.getFirstName();
        if (max_names <= 0)
            return firstName;
        if (firstName.trim().equals(""))
            return "";

        String[] names = firstName.split("  *");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < max_names && i < names.length; i++)
            sb.append(names[i]).append(" ");

        return sb.substring(0, sb.length() - 1);
    }

    
    private boolean isAlive(Indi indi, Date date)
    {
        if (indi.getDeathDate() != null || indi.getProperty(PATH_INDIDEATPLAC) != null)
            return false;
        if (assume_dead == 0)
            return true;

        PropertyDate birth = indi.getBirthDate();
        if (birth == null)
            return true;
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int d = cal.get(Calendar.DATE);
        Delta delta =  birth.getAnniversary(new PointInTime(d-1, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR)));
        if (delta == null)
            return true;
        return (delta.getYears() < assume_dead);
    }

    
    private static class Event
    {
        public Date date;
        public int count;

        public Event(Date date, int count)
        {
            this.date = date;
            this.count = count;
        }

    }
}
