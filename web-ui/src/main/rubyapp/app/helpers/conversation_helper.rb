module ConversationHelper
  # Loads or creates a new conversation object and populates the data for it, passed as parameters
  def build_conversation(params)
    name = params[:name]
    description = params[:description]
    conversation = Conversation.find(params[:id])

    if (conversation.nil?)
      conversation = Conversation.new 
    end

    inbound_transport = Transport.new
    inbound_transport.transport_type_id = (TransportType.find_by_class_name params[:inbound_transport_type]).object_id

    outbound_transport = Transport.new
    outbound_transport.transport_type_id = (TransportType.find_by_class_name params[:outbound_transport_type]).object_id

    inbound_format = Format.new
    inbound_format.format_type_id = (FormatType.find_by_class_name params[:inbound_format_type]).object_id

    outbound_format = Format.new
    outbound_format.format_type_id = (FormatType.find_by_class_name params[:outbound_format_type]).object_id

    conversation.name=name;
    conversation.description=description;
    conversation.system_id=params[:system_id];

    conversation.in_transport = inbound_transport;
    conversation.in_format = inbound_format;

    conversation.out_transport = outbound_transport;
    conversation.out_format = outbound_format;

    return conversation

  end
end
