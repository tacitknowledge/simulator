require 'singleton'
require 'java'
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
    @conv_mgr = ConversationManagerImpl.new
  end

  def get_format_parameters(format)
    @conv_mgr.getAdapterParameters(format)
  end

  def get_transport_parameters(type)
    @conv_mgr.getTransportParameters(type)
  end

  def create_conversation
    #
  end

  def create_conversation_scenario
    #
  end

  def activate
    #
  end

  def deactivate
    #
  end

  def delete_conversation
    #
  end
end