require 'singleton'
require 'java'
Dir["lib/*.jar"].each {|jar|
  require jar
}
require "log4j-1.2.14.jar"
require "camel-core-2.0.0.jar"
require "simulator-core-1.0-SNAPSHOT.jar"
include_class 'com.tacitknowledge.simulator.impl.ConversationManagerImpl'


# This class is to be the sole communication way from the JRuby app into the Java Simulator.
# It should provide methods to access all the functionality provided by ConversationManager.
# Also, this class should handle all Java-classes that are to be populated and passed to the
# ConversationManager methods.
class SimulatorConnector
  include Singleton

  def initialize
    @conv_mgr = ConversationManagerImpl.getInstance()
  end

  def get_format_parameters(format)
    @conv_mgr.getAdapterParameters(format)
  end

  def get_transport_parameters(type)
    @conv_mgr.getTransportParameters(type)
  end


  def create_conversation (conversation)
    in_transport = @conv_mgr.getClassByName( conversation.in_transport.transport_type.class_name )

    parameters = java.util.HashMap.new;

    conversation.in_transport.configurations.each do |configuration|
      parameters.put(configuration.attribute_name, configuration.attribute_value)
    end
    in_transport.setParameters(parameters)


    out_transport = @conv_mgr.getClassByName( conversation.out_transport.transport_type.class_name )
    parameters = java.util.HashMap.new;
    conversation.out_transport.configurations.each do |configuration|
      parameters.put(configuration.attribute_name, configuration.attribute_value)
    end
    out_transport.setParameters(parameters)




    in_adapter = @conv_mgr.getClassByName(conversation.in_format.format_type.class_name )
    parameters = java.util.HashMap.new;
    conversation.in_format.configurations.each do |configuration|
      parameters.put(configuration.attribute_name, configuration.attribute_value)
    end
    in_adapter.setParameters(parameters)




    out_adapter = @conv_mgr.getClassByName(conversation.out_format.format_type.class_name)
    parameters = java.util.HashMap.new;
    conversation.out_format.configurations.each do |configuration|
      parameters.put(configuration.attribute_name, configuration.attribute_value)
    end
    out_adapter.setParameters(parameters)


    @conv_mgr.createConversation(conversation.id, in_transport, out_transport, in_adapter, out_adapter)
  end

  def create_conversation_scenario
    #
  end

  def activate(conversation)
    #
    if (!is_active(conversation))
      jconvers=create_conversation(conversation)
      conversation.scenarios.each do |scenario|
        jconvers.addScenario('javascript',scenario.criteria_script,scenario.execution_script)
      end
      @conv_mgr.activate(jconvers.getId())
    end
  end


  def is_active(conversation)
    isActive = @conv_mgr.isActive(conversation.id)
    return isActive
  end

  def deactivate(conversation)
    #
    @conv_mgr.deactivate(conversation.id)
  end

  def delete_conversation(conversation)
    #
  end

  def available_languages
    jlanguages = @conv_mgr.getAvailableLanguages()
    languages =[]
    for i in (0..(jlanguages.length-1))
         languages<<{
                 :id=>jlanguages[i][0]
#         ,
#                 :language=>jlanguages[i][1]
                 }
    end
    return languages
  end
end