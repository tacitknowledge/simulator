require 'singleton'
require 'java'
Dir["lib/*.jar"].each {|jar|
  require jar
}
#require "log4j-1.2.14.jar"
#require "camel-core-2.0.0.jar"
#require "simulator-core-1.0-SNAPSHOT.jar"
include_class 'com.tacitknowledge.simulator.impl.ConversationManagerImpl'


# This class is to be the sole communication way from the JRuby app into the Java Simulator.
# It should provide methods to access all the functionality provided by ConversationManager.
# Also, this class should handle all Java-classes that are to be populated and passed to the
# ConversationManager methods.
class SimulatorConnector
  include Singleton

  def initialize
    @conv_mgr = ConversationManagerImpl.new
  end

  def get_format_parameters(format)
    @conv_mgr.getAdapterParameters(format)
  end

  def get_transport_parameters(class_name)
    @conv_mgr.getTransportParameters(class_name)
  end


  def create_or_update_conversation (conversation)

    in_transport = @conv_mgr.getClassByName( conversation.in_transport.transport_type.class_name )
    set_parameters(in_transport, conversation.in_transport.configurations)

    out_transport = @conv_mgr.getClassByName( conversation.out_transport.transport_type.class_name )
    set_parameters(out_transport,conversation.out_transport.configurations)

    in_adapter = @conv_mgr.getClassByName(conversation.in_format.format_type.class_name )
    set_parameters(in_adapter,conversation.in_format.configurations)


    out_adapter = @conv_mgr.getClassByName(conversation.out_format.format_type.class_name)
    set_parameters(out_adapter,conversation.out_format.configurations)

    @conv_mgr.createOrUpdateConversation(conversation.id, conversation.name, in_transport, out_transport, in_adapter, out_adapter, conversation.default_response)
  end


  def set_parameters(jobject, configurations)
    parameters = java.util.HashMap.new;
    configurations.each do |configuration|
      parameters.put(configuration.attribute_name, configuration.attribute_value)
    end
    jobject.setParameters(parameters)
  end


  def create_or_update_conversation_scenario(scenario)
    system = System.find(scenario.conversation.system_id)
    script_language = system.script_language
    @conv_mgr.createOrUpdateConversationScenario(scenario.conversation_id, scenario.id,  script_language, scenario.criteria_script, scenario.execution_script)
  end                                                                                      

  def activate(conversation)
    #
    if (!is_active(conversation))
      system = System.find(conversation.system_id)
      script_language = system.script_language

      exists = @conv_mgr.conversationExists(conversation.id)
# if conversation is not already in simulator then create it and activate. otherwise just activate     
      if (!exists)
        jconvers=create_or_update_conversation(conversation)
        conversation.scenarios.each do |scenario|
          jconvers.addOrUpdateScenario( scenario.id, script_language, scenario.criteria_script, scenario.execution_script)
        end
        @conv_mgr.activate(conversation.id)
        return jconvers
      end
      @conv_mgr.activate(conversation.id)
    end
  end


  def is_active(conversation)
    return @conv_mgr.isActive(conversation.id)
  end

  def deactivate(conversation)
    #
    @conv_mgr.deactivate(conversation.id)
  end

  def delete_conversation(conversation)
    #
    @conv_mgr.deleteConversation(conversation.id);
  end

  def delete_scenario(scenario)
    #
    @conv_mgr.deleteScenario(scenario.conversation_id, scenario.id);
  end

  def available_languages
    jlanguages = @conv_mgr.getAvailableLanguages()
    languages =[]
    for i in (0..(jlanguages.length-1))
      languages<<{
              :id=>jlanguages[i][0]
      }
    end
    return languages
  end

  def register_listeners(file_path)
    @conv_mgr.registerListeners(file_path)
  end
end