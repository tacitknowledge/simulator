class ConversationController < ApplicationController

  def remove
    id = params[:conversation_id];
    Conversation.delete(id);
  end


  def save
    name = params[:name]
    description = params[:description]
    conversation = Conversation.find_by_name name

    if (conversation.nil?)

      inbound_transport = Transport.new
      inbound_transport.transport_type_id = (TransportType.find_by_class_name params[:inbound_transport_type]).object_id
#      inbound_transport.save

      outbound_transport = Transport.new
      outbound_transport.transport_type_id = (TransportType.find_by_class_name params[:outbound_transport_type]).object_id
#      outbound_transport.save


      inbound_format = Format.new
      inbound_format.format_type_id = (FormatType.find_by_class_name params[:inbound_format_type]).object_id
#      inbound_format.save

      outbound_format = Format.new
      outbound_format.format_type_id = (FormatType.find_by_class_name params[:outbound_format_type]).object_id
#      outbound_format.save


      new_conversation = Conversation.new
      new_conversation.name=name;
      new_conversation.description=description;
      new_conversation.system_id=params[:system_id];

      new_conversation.in_transport = inbound_transport;
      new_conversation.in_format = inbound_format;

      new_conversation.out_transport = outbound_transport;
      new_conversation.out_format = outbound_format;

      new_conversation.save

      render :json =>{:success => true, :mgs => 'Saved with id ' + new_conversation.id.to_s}, :status => 200
    else
      render :json =>{:success => false, :msg => 'Conversation with name "'+name+'" already exists'}, :status => 200
    end
  end


  def list
    system_id =params[:system_id]
    all_by_system_id = Conversation.find_all_by_system_id(system_id);

    all_by_system_id.each do |conversation|
      tmp={}
      tmp['name']=conversation.name
      tmp['description']=conversation.description;
#      tmp['iTransport']=conversation.inboundTransport.name;
#      tmp['oTransport']=conversation.outboundTransport.name;
#      tmp['iFormat']=conversation.inboundFormat.type;
#      tmp['oFormat']=conversation.outboundFormat.type;
      systems_hash[:systems]<<tmp
    end
    render :json => systems_hash.to_json
  end


end
