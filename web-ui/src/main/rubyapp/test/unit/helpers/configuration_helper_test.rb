require 'test_helper'
require "rexml/document"

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



  test "import" do

    xml = "<?xml version='1.0'?>
<systems>
    <system>
        <name>New System</name>
        <description>mydescription</description>
        <script_language>javascript</script_language>
        <conversation>
            <name>New Conversation</name>
            <description>conversationdescription</description>
            <default_response>asdfasdf</default_response>
            <enabled>true</enabled>
            <inbound_transport>
                <type>File</type>
                <configuration>
                    <directoryName>input</directoryName>
                    <fileName>test.xml</fileName>
                    <pollingInterval>600</pollingInterval>
                </configuration>
                <type>File</type>
            </inbound_transport>
            <outbound_transport>
                <type>File</type>
                <configuration>
                    <directoryName>output</directoryName>
                    <fileName>output.xml</fileName>
                    <pollingInterval>500</pollingInterval>
                </configuration>
            </outbound_transport>
            <inbound_format>
                <type>XML</type>
                <configuration>
                   <testPropertyName>testPropertyValue</testPropertyName>
                </configuration>
            </inbound_format>
            <outbound_format>
                <type>XML</type>
                <configuration/>
            </outbound_format>
            <scenario>
                <name>1234</name>
                <label/>
                <criteria_script>true</criteria_script>
                <execution_script>var xxx ={xxx:1234}
                    xxx
                </execution_script>
                <enabled>true</enabled>
            </scenario>
        </conversation>
    </system>
</systems>"

    doc = REXML::Document.new(xml)
    import_xml(doc)

    system = System.find_by_name("New System")
    assert_not_nil(system)
    assert_equal("mydescription", system.description)
    assert_equal("javascript", system.script_language)

    conversation = Conversation.find_by_system_id(system.id)
    assert_equal("conversationdescription", conversation.description)
    assert_equal("asdfasdf", conversation.default_response)
    assert_equal("File", conversation.in_transport.transport_type.name)
    configurations = conversation.in_transport.configurations

    configurations.each do|configuration|
      case configuration.attribute_name
        when "directoryName"
          assert_equal "input", configuration.attribute_value
        when "fileName"
          assert_equal "test.xml", configuration.attribute_value
        when "pollingInterval"
          assert_equal "600", configuration.attribute_value
        else
      end
    end

    assert_equal("File", conversation.out_transport.transport_type.name)
    configurations = conversation.out_transport.configurations
    configurations.each do|configuration|
      case configuration.attribute_name
        when "directoryName"
          assert_equal "output", configuration.attribute_value
        when "fileName"
          assert_equal "output.xml", configuration.attribute_value
        when "pollingInterval"
          assert_equal "500", configuration.attribute_value
        else
          fail()
      end
    end

    assert_equal("XML", conversation.in_format.format_type.name)
    configurations = conversation.in_format.configurations
    configurations.each do|configuration|
      case configuration.attribute_name
        when "testPropertyName"
          assert_equal "testPropertyValue", configuration.attribute_value
        else
          fail()
      end
    end

    assert_equal("XML", conversation.out_format.format_type.name)
    configurations = conversation.out_format.configurations
    configurations.each do|configuration|
      fail()
    end
  end
end
