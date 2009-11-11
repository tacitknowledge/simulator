class ConversationController < ApplicationController

  def remove
    id = params[:conversation_id];
    Conversation.delete(id);
  end

end
