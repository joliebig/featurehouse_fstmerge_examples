using System;
using System.ComponentModel;
namespace RssBandit.AppServices
{
 public interface IPropertyChange {
  event PropertyChangedEventHandler PropertyChanged;
 }
 public interface IInternetService
 {
  event InternetConnectionStateChangeHandler InternetConnectionStateChange;
  bool InternetAccessAllowed { get; }
  bool InternetConnectionOffline { get; }
  INetState InternetConnectionState { get; }
 }
}
