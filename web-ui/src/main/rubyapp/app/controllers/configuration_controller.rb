require "rexml/document"
#Configuration controller is responsible for importing/exporting system configuration from/to xml files
#
class ConfigurationController < ApplicationController

  include ConfigurationHelper

# this magic method fixes file upload problems.
  protect_from_forgery :only => [:create, :update, :destroy]

  def export
    systems = System.find(:all)

    #doc - REXML document
    doc = systems_to_xml(systems)
    #todo add error handling

    response.headers["Content-Type"] = 'application/xml'
    render :xml => doc.to_s;
  end

#
#
#
  def import
    file_path = params[:upload].local_path
    file = File.new(file_path)
    doc = REXML::Document.new file
    doc.root.elements.each do |system_element|
       name = get_property(system_element, 'name')
       system = System.find_by_name name
       if(system.nil?)
         system = System.new
         system.name = name;
         is_new=true
       else
         is_new=false
       end
       system.description = get_property(system_element, 'description')
       system.script_language = get_property(system_element, 'script_language')
       system.save

#      parse conversation
       parse_conversations(is_new, system, system_element)
    end
     render :template => "success"
  end

  private

  def parse_conversations(is_new, system, system_element)
    conversations_elements = system_element.get_elements("conversation")
    conversations_elements.each do |conversation_element|
      conversation_name = get_property(conversation_element, "name")
      if (is_new)
        conversation = Conversation.new
        conversation.name=conversation_name
        conversation.system_id = system.id
      else
        conversation = Conversation.find_by_name_and_system_id(conversation_name, system.id)
        if (conversation.nil?)
          conversation = Conversation.new
          conversation.name = conversation_name
          conversation.system_id = system.id
        end
      end
      conversation.description=get_property(conversation_element, "description")

      in_transport_element = conversation_element.get_elements("inbound_transport")[0]
      out_transport_element = conversation_element.get_elements("outbound_transport")[0]
      in_format_element = conversation_element.get_elements("inbound_format")[0]
      out_format_element = conversation_element.get_elements("outbound_format")[0]

      conversation.in_transport = parse_transport in_transport_element
      conversation.out_transport = parse_transport out_transport_element
      conversation.in_format = parse_format in_format_element
      conversation.out_format = parse_format out_format_element
      conversation.save

#     parse scenarios for each conversation
      parse_scenarios(conversation, is_new, conversation_element)
    end
  end

  def parse_scenarios(conversation, is_new, conversation_element)
    scenario_elements = conversation_element.get_elements("scenario")
    scenarios=[]
    scenario_elements.each do |scenario_element|
      scenario_name = get_property(scenario_element, "name")
      if (is_new)
        scenario = Scenario.new
        scenario.name=scenario_name
        scenario.conversation_id = conversation.id
      else
        scenario = Scenario.find_by_conversation_id_and_name(conversation.id, scenario_name)
        if(scenario.nil?)
          scenario = Scenario.new
          scenario.name=scenario_name
          scenario.conversation_id = conversation.id
        end
      end
      scenario.label=get_property(scenario_element, "label")
      scenario.criteria_script=get_property(scenario_element, "criteria_script")
      scenario.execution_script=get_property(scenario_element, "execution_script")
      scenario.save
      scenarios<<scenario
    end
    scenarios
  end


  def parse_format(format_element)
    format_type_name = get_property(format_element,"type")
    format_type = FormatType.find_by_name(format_type_name)

    format = Format.new
    format.format_type_id = format_type.id

    configuration_element = format_element.get_elements("configuration")[0]
    configurations=[]
    configuration_element.elements.each do |config_name|
      config=FormatConfiguration.new
      config.attribute_name = config_name.name
      config.attribute_value = config_name.text
      configurations << config
    end
    format.configurations = configurations
    format
  end

  def parse_transport(transport_element)
    transport_type_name = get_property(transport_element,"type")
    transport_type = TransportType.find_by_name(transport_type_name)

    transport = Transport.new
    transport.transport_type_id = transport_type.id

    configuration_element = transport_element.get_elements("configuration")[0]
    configurations=[]

    configuration_element.elements.each do |config_name|
      config=TransportConfiguration.new
      config.attribute_name = config_name.name 
      config.attribute_value=config_name.text
      configurations<<config
    end

    transport.configurations=configurations
    transport
  end

  def get_property(element,property_name)
   element.get_elements(property_name)[0].text
  end




 
end
