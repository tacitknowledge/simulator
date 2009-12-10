module ConversationHelper
  # Loads or creates a new conversation object and populates the data for it, passed as parameters
  def build_conversation(params)
    
    return conversation

  end

  def populate_conversation(params)
    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type_id]
    out_tt = params[:outbound_transport_type_id]
    in_ft = params[:inbound_format_type_id]
    out_ft = params[:outbound_format_type_id]
    sys_id = params[:system_id]
    default_response = params[:default_response]
    conversation = Conversation.new
    
    inbound_transport = Transport.new
    inbound_transport.transport_type = TransportType.find in_tt
    inbound_transport.configurations = get_configurations('transport', 'in', params)

    outbound_transport = Transport.new
    outbound_transport.transport_type = TransportType.find out_tt
    outbound_transport.configurations = get_configurations('transport', 'out', params)

    inbound_format = Format.new
    inbound_format.format_type = FormatType.find in_ft
    inbound_format.configurations = get_configurations('format', 'in', params)

    outbound_format = Format.new
    outbound_format.format_type = FormatType.find out_ft
    outbound_format.configurations = get_configurations('format', 'out', params)

    conversation.name = name;
    conversation.description = description;
    conversation.system_id = sys_id;
    conversation.default_response=default_response

    conversation.in_transport = inbound_transport;
    conversation.in_format = inbound_format;

    conversation.out_transport = outbound_transport;
    conversation.out_format = outbound_format;

    conversation
  end

  def update_conversation(params, conversation)
    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type_id]
    out_tt = params[:outbound_transport_type_id]
    in_ft = params[:inbound_format_type_id]
    out_ft = params[:outbound_format_type_id]
    sys_id = params[:system_id]
    default_response = params[:default_response]

    conversation.name = name;
    conversation.description = description;
    conversation.system_id = sys_id;
    conversation.default_response = default_response

    #conversation.in_transport.transport_type_id = in_tt
    trans_in = conversation.in_transport
    trans_in.transport_type_id = in_tt
    trans_in.configurations = get_configurations('transport', 'in', params)
    
    #conversation.out_transport.transport_type_id = out_tt
    trans_out = conversation.out_transport
    trans_out.transport_type_id = out_tt
    trans_out.configurations = get_configurations('transport', 'out', params)

    #conversation.in_format.format_type_id = in_ft
    format_in = conversation.in_format
    format_in.format_type_id = in_ft
    format_in.configurations = get_configurations('format', 'in', params)

    #conversation.out_format.format_type_id =out_ft
    format_out = conversation.out_format
    format_out.format_type_id = out_ft
    format_out.configurations = get_configurations('format', 'out', params)

    conversation
  end

  def get_configurations(config_type, in_out, params)
    
    configurations = []

    prefix = "#{config_type}_#{in_out}_"
    strt_idx = prefix.length
    params.each{|param_name, param_value|
      # Make sure we only add non-empty configuration values to the list
      if (!param_value.empty?)
        if (param_name.include? prefix)
          # If the prefix was found, this parameter is part of this adapter configutation

          if (config_type.eql? 'format')
            config = FormatConfiguration.new
          else
            config = TransportConfiguration.new
          end

          # Attribute name
          attr_name = param_name[strt_idx, param_name.length - strt_idx]

          config.attribute_name = attr_name
          config.attribute_value = param_value

          configurations << config
        end
      end
    }
    configurations
  end
end
