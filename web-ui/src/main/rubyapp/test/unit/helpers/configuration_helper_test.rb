require 'test_helper'

class ConfigurationHelperTest < ActionView::TestCase
  
  test "export" do
    systems = System.find(:all)
    doc = systems_to_xml(systems)
    assert(!doc.nil?)
    puts doc.root
    assert_equal("systems", doc.root.name)
    assert_equal(2, doc.root.get_elements("system").length)
    first_system_element = doc.root.get_elements("system")[0]
    assert_equal("system", first_system_element.name)
    assert_equal("system", doc.root.get_elements("system")[1].name)
    assert_equal("Project 1", first_system_element.get_elements("name")[0].text)
    assert_equal("Description 1", first_system_element.get_elements("description")[0].text)
    assert_equal("javascript", first_system_element.get_elements("script_language")[0].text)
    assert_equal(2, first_system_element.get_elements("conversation").length)
    first_conversation = first_system_element.get_elements("conversation")[0]
    assert_equal("conversation", first_conversation.name)
    assert_equal("My test conversation", first_conversation.get_elements("name")[0].text)
    assert_equal("Something", first_conversation.get_elements("description")[0].text)
    assert_equal("puts 'hello world'", first_conversation.get_elements("default_response")[0].text)
    assert_equal("File", first_conversation.get_elements("inbound_transport")[0].get_elements("type")[0].text)
    assert_equal(1, first_conversation.get_elements("inbound_transport")[0].get_elements("configuration").length)
    assert_equal("inbox", first_conversation.get_elements("inbound_transport")[0].get_elements("configuration")[0].get_elements("directoryName")[0].text)
    assert_equal("FTP", first_conversation.get_elements("outbound_transport")[0].get_elements("type")[0].text)
    assert_equal("XML", first_conversation.get_elements("inbound_format")[0].get_elements("type")[0].text)
    assert_equal("CSV", first_conversation.get_elements("outbound_format")[0].get_elements("type")[0].text)
  end
end
