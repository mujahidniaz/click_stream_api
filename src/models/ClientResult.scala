package models

class ClientResult {
  var Users: Long = _
  var Clicks: Long = _
  var Impressions: Long = _
  
  // setters
  def unique_users = Users
  def clicks = Clicks
  def impressions = Impressions

  // setters

  def unique_users_=(u: Long) = { Users = u }
  def clicks_=(c: Long) = { Clicks = c }
  def impressions_=(i: Long) = { Impressions = i }
}