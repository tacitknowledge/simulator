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

    conversation = Conversation.new
    
    inbound_transport = Transport.new
    inbound_transport.transport_type = TransportType.find in_tt
    inbound_transport.transport_configurations = get_configurations('transport', 'in', params)

    outbound_transport = Transport.new
    outbound_transport.transport_type = TransportType.find out_tt
    outbound_transport.transport_configurations = get_configurations('transport', 'out', params)

    inbound_format = Format.new
    inbound_format.format_type = FormatType.find in_ft
    inbound_format.format_configurations = get_configurations('format', 'in', params)

    outbound_format = Format.new
    outbound_format.format_type = FormatType.find out_ft
    outbound_format.format_configurations = get_configurations('format', 'out', params)

    conversation.name = name;
    conversation.description = description;
    conversation.system_id = sys_id;

    conversation.in_transport = inbound_transport;
    conversation.in_format = inbound_format;

    conversation.out_transport = outbound_transport;
    conversation.out_format = outbound_format;

    conversation
  end

  def update_conversation(params)
    name = params[:name]
    description = params[:description]
    in_tt = params[:inbound_transport_type_id]
    out_tt = params[:outbound_transport_type_id]
    in_ft = params[:inbound_format_type_id]
    out_ft = params[:outbound_format_type_id]
    sys_id = params[:system_id]

    conversation = Conversation.find params[:id]

    conversation.name = name;
    conversation.description = description;
    conversation.system_id = sys_id;

    conversation.in_transport.transport_type_id = in_tt
    
    conversation.out_transport.transport_type_id = out_tt

    conversation.in_format.format_type_id = in_ft

    conversation.out_format.format_type_id =out_ft

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
