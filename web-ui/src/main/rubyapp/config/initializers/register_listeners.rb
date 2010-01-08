if (!ENV["RAILS_ENV"].eql?("test"))
  begin
    puts 'Registering event listeners'
    SimulatorConnector.instance.register_listeners(ENV["SIMULATOR_LISTENER_FILE_PATH"])
  rescue => e
    puts e.message
  end
end
