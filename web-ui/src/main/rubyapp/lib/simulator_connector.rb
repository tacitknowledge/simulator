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
#    in_transport = @conv_mgr.getClassByName( conversation.in_transport.transport_type.class_name )
#    out_transport = @conv_mgr.getClassByName( conversation.out_transport.transport_type.class_name )
#    in_adapter = @conv_mgr.getClassByName(conversation.in_format.format_type.class_name )
#    out_adapter = @conv_mgr.getClassByName(conversation.out_format.format_type.class_name)

    in_transport = @conv_mgr.getClassByName('com.tacitknowledge.simulator.transports.FileTransport')
    parameters = java.util.HashMap.new;
    parameters.put('directoryName', '12345')
    parameters.put('fileName', 'zzzz.properties')
    in_transport.setParameters(parameters)
    in_transport.setDeleteFile(true)

    out_transport = @conv_mgr.getClassByName('com.tacitknowledge.simulator.transports.FileTransport')
    parameters = java.util.HashMap.new;
    parameters.put('directoryName', '123456')
    parameters.put('fileName', 'zzzz.properties')
    
    out_transport.setParameters(parameters)
    out_transport.setDeleteFile(true)


    in_adapter = @conv_mgr.getClassByName('com.tacitknowledge.simulator.formats.PropertiesAdapter')
    out_adapter = @conv_mgr.getClassByName('com.tacitknowledge.simulator.formats.PropertiesAdapter')


    @conv_mgr.createConversation(conversation.id, in_transport, out_transport, in_adapter, out_adapter)
  end

  def create_conversation_scenario
    #
  end

  def activate(conversation)
    #
    if (!is_active(conversation))
      convers=create_conversation(conversation)
      @conv_mgr.activate(convers.getId())
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
end