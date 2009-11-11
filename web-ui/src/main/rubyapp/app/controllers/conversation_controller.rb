class ConversationController < ApplicationController

  def remove
    id = params[:conversation_id];
    Conversation.delete(id);
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
