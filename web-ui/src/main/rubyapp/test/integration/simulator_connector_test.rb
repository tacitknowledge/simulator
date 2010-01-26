require 'test_helper'

class SimualtorConnectorTest < ActionController::IntegrationTest
  fixtures :all

  def test_available_languages
    available_languages = SimulatorConnector.instance.available_languages
    assert(available_languages[0][:id].eql?('javascript'))
    assert(available_languages[1][:id].eql?('ruby'))
  end

  def test_create_conversation
    conversation = Conversation.find 2
    puts conversation
    jconversation = SimulatorConnector.instance.create_or_update_conversation(conversation)
    assert_not_nil(jconversation)
  end

  def test_activate_deactivate
    conversation = Conversation.find(2)
    jconversation = SimulatorConnector.instance.create_or_update_conversation(conversation)
    jconversation = SimulatorConnector.instance.activate(conversation)
    #assert_not_nil(jconversation)

    active = SimulatorConnector.instance.is_active(conversation)
    assert(active)
    
    jconversation = SimulatorConnector.instance.deactivate(conversation)
    active = SimulatorConnector.instance.is_active(conversation)
    assert(!active)

    jconversation = SimulatorConnector.instance.activate(conversation)

    active = SimulatorConnector.instance.is_active(conversation)
    assert(active)
  end

  def test_delete_conversation
    conversation = Conversation.find(2)
    jconversation = SimulatorConnector.instance.activate(conversation)
    assert(SimulatorConnector.instance.is_active(conversation))
    SimulatorConnector.instance.delete_conversation(conversation)
    assert(!SimulatorConnector.instance.is_active(conversation))
  end

end
