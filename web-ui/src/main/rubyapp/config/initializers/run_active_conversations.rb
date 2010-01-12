if (!ENV["RAILS_ENV"].eql?("test"))
  begin
    enabled_conversations = Conversation.find_all_by_enabled(true)
    puts 'Activating all enabled conversations'
    enabled_conversations.each do |conversation|
      puts conversation.name
      SimulatorConnector.instance.activate(conversation);
    end
  rescue => e
    #puts e.message
  end
end
