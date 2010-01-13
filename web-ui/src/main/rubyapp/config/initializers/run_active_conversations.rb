if (!ENV["RAILS_ENV"].eql?("test"))
  begin
    all_conversations = Conversation.find :all
    #Add all conversations to the SimulatorConnector
    all_conversations.each do |conversation|
        SimulatorConnector.instance.create_conversation(conversation)
    end

    enabled_conversations = Conversation.find_all_by_enabled(true)
    puts 'Activating all enabled conversations'
    enabled_conversations.each do |conversation|
      SimulatorConnector.instance.activate(conversation);
    end
  rescue => e
    puts e.message
  end
end
