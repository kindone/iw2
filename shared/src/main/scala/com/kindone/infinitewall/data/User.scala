package com.kindone.infinitewall.data;

case class UserForm(email: String, password: String)

case class ChangePasswordForm(oldPassword:String, newPassword:String)