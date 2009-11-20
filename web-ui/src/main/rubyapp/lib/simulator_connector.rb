require 'singleton'
require 'java'
Dir["lib/*.jar"].each {|jar|
  require jar
}
require "log4j-1.2.14.jar"
require "camel-core-2.0.0.jar"
require "simulator-core-1.0-SNAPSHOT.jar"
include_class 'com.tacitknowledge.simulator.impl.ConversationManagerImpl'
#include_package 'com.tacitknowledge.simulator'

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

  def get_transport_parameters(type)
    @conv_mgr.getTransportParameters(type)
  end


  def create_conversation (conversation)
    in_transport = @conv_mgr.getClassByName( conversation.in_transport.transport_type.class_name )
    out_transport = @conv_mgr.getClassByName( conversation.out_transport.transport_type.class_name )
    in_adapter = @conv_mgr.getClassByName(conversation.in_format.format_type.class_name )
    out_adapter = @conv_mgr.getClassByName(conversation.out_format.format_type.class_name)
    @conv_mgr.createConversation(conversation.id, in_transport, out_transport, in_adapter, out_adapter)
  end

  def create_conversation_scenario
    #
  end

  def activate
    #
  end


  def is_active(conversation)
   @conv_mgr.is_active(conversation.id)
  end

  def deactivate
    #
  end

  def delete_conversation
    #
  end
end