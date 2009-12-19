require "rexml/document"

class ConfigurationController < ApplicationController

  before_filter :header_xml

  def header_xml
    response.headers['Content-type'] = 'text/xml; charset=utf-8'
  end
  def export
    systems = System.find(:all)

    doc = REXML::Document.new("<?xml version='1.0?><systems/>")

#    doc.root = REXML::Element.new("systems");

    systems.each do |system|
      system_element = doc.root.add_element "system"

      create_element system_element, "name", system.name
      create_element system_element, "description", system.description
      create_element system_element, "script_language", system.script_language

      conversations = Conversation.find_all_by_system_id(system.id)
      conversations.each do |conversation|
        conversation_element = system_element.add_element "conversation"

        create_element conversation_element, "name", conversation.name
        create_element conversation_element, "description", conversation.description
        create_element conversation_element, "default_response", conversation.default_response.to_s
        create_element conversation_element, "enabled", conversation.enabled.to_s

        convert_transport conversation_element.add_element("inbound_transport"), conversation.in_transport
        convert_transport conversation_element.add_element("outbound_transport"), conversation.out_transport
        convert_format conversation_element.add_element("inbound_format"), conversation.in_format
        convert_format conversation_element.add_element("outbound_format"), conversation.out_format

        scenarios = Scenario.find_all_by_conversation_id conversation.id
        scenarios.each do |scenario|
          scenario_element = conversation_element.add_element "scenario"
          create_element scenario_element, "name", scenario.name
          create_element scenario_element, "label", scenario.label
          create_element scenario_element, "criteria_script", scenario.criteria_script
          create_element scenario_element, "execution_script", scenario.execution_script
          create_element scenario_element, "enabled", scenario.enabled
        end
      end
    end

    response.headers["Content-Type"] = 'text/xml'
    render :xml => doc.to_s;
  end
  
  private

  def convert_transport(transport_element, transport)
    create_element transport_element, "type", transport.transport_type.name
    configuration_element = transport_element.add_element("configuration")
    transport.configurations.each do |configuration|
      create_element configuration_element, configuration.attribute_name, configuration.attribute_value
    end
  end

  def convert_format(format_element, format)
    create_element format_element, "type", format.format_type.name
    configuration_element = format_element.add_element("configuration")
    format.configurations.each do |configuration|
      create_element configuration_element, configuration.attribute_name, configuration.attribute_value
    end
  end


  def create_element parent, tag_name, value
      system_name_element = parent.add_element(tag_name)
      system_name_element.text=value
      system_name_element
  end

end
