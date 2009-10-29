# Be sure to restart your server when you modify this file.

# Your secret key for verifying cookie session data integrity.
# If you change this key, all old sessions will become invalid!
# Make sure the secret is at least 30 characters and all random, 
# no regular words or you'll be exposed to dictionary attacks.
ActionController::Base.session = {
  :key         => '_rubyapp_session',
  :secret      => '2553f28419318bdc2469078a1e703364805fca78ce5d7a2c77578eb79bd269f72b07cad81170335b9a5e1b3cfdc073e24702325f2ca3fff45846e20865dc6556'
}

# Use the database for sessions instead of the cookie-based default,
# which shouldn't be used to store highly confidential information
# (create the session table with "rake db:sessions:create")
# ActionController::Base.session_store = :active_record_store
