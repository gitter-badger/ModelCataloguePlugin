#= require_self
#= require rest
#= require enhance
#= require recursiveCompile
#= require names
#= require messages
#= require security

angular.module 'mc.util', [
  # list of modules
  'mc.util.enhance'
  'mc.util.rest'
  'mc.util.names'
  'mc.util.security'
  'mc.util.messages'
  'mc.util.recursiveCompile'
]