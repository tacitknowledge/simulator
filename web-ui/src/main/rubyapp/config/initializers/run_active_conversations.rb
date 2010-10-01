=begin
This script is run during application deployment and it always hangs the build if there are any enabled conversations.
It should probably be run during server startup only!
Commenting out until issue is resolved
if (!ENV["RAILS_ENV"].eql?("test"))
  begin
    all_conversations = Conversation.find :all
    #Add all conversations to the SimulatorConnector
    all_conversations.each do |conversation|
        jconvers = SimulatorConnector.instance.create_or_update_conversation(conversation)
        conversation.scenarios.each do |scenario|
            system = System.find(scenario.conversation.system_id)
            script_language = system.script_language
            jconvers.addOrUpdateScenario( scenario.id, script_language, scenario.criteria_script, scenario.execution_script)
        end
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
=end
