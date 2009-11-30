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
    jconversation = SimulatorConnector.instance.create_conversation(conversation)
    assert_not_nil(jconversation)
  end
end
