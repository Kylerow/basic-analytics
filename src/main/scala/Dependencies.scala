
trait Dependencies {
  var _statistics :Statistics = null
  def statistics: Statistics = {
    if (_statistics==null) _statistics = new Statistics
    _statistics
  }

  var _eventStorage :EventStorage = null
  def eventStorage = {
    if (_eventStorage==null) _eventStorage = new EventStorage
    _eventStorage
  }
}
