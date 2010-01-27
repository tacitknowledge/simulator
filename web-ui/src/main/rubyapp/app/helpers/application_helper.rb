# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  #Method used in views to build the path to the ext js files
  #based on the configuration set in the environment variables.
  def relative_url_root
    ENV["relative_url_root"]
  end

  #Generates the application breadcrumb based on url resources requested
  def get_breadcrumb(params)
    result = ""
    system_id = params[:system_id]
    action=params[:action]

    if (system_id.nil?)
      system_id=params[:id]
      if (action.eql? 'new')
        system_name = 'New system'
      elsif (action.eql? 'show')
        system_name = 'Edit system'
      end
    else
      conversation_id=params[:conversation_id]
      if conversation_id.nil?
        conversation_id = params[:id]

        if (action.eql? 'new')
          conversation_name = 'New conversation'
        else
          conversation_name = 'Edit conversation'
        end
      else
        scenario_id=params[:scenario_id]
        if (scenario_id.nil?)
          scenario_id = params[:id]
          if (action.eql? 'new')
            scenario_name = 'New scenario'
          else
            scenario_name = 'Edit scenario'
          end

        end
      end
    end
     
    result << "<a href='#{relative_url_root}/'>Home</a>"

    if (!system_id.nil?)
      tn=system_name
      system_name=system_name.nil? ? (System.find system_id).name: system_name;
      result << " &gt; "

      if (!tn.nil?)
        result << "<span>#{system_name}</span>"
      else
        result << "<a href='#{relative_url_root}/systems/#{system_id}/'>#{system_name}</a>"
      end
    else
      if (!system_name.nil?)
        result << " &gt; <span>#{system_name}</span>"
      end
    end


    if (!conversation_id.nil?)
      tn=conversation_name
      conversation_name = conversation_name.nil? ? (Conversation.find conversation_id).name : conversation_name

      result << " &gt; "
      if (tn.nil?)
        result << "<a href='#{relative_url_root}/systems/#{system_id}/conversations/#{conversation_id}/'>#{conversation_name}</a>"
      else
        result << "<span>#{conversation_name}</span>"
      end
    elsif (!conversation_name.nil?)
      result << " &gt; <span>#{conversation_name}</span>"
    end


    if (!scenario_id.nil?)
      tn=scenario_name
      scenario_name =scenario_name.nil? ? (Scenario.find scenario_id).name: scenario_name

      result << " &gt; "
      if (tn.nil?)
        result << "<a href='#{relative_url_root}/systems/#{system_id}/conversations/#{conversation_id}/scenarios/#{scenario_id}/'>#{scenario_name}</a>"
      else
        result << "<span>#{scenario_name}</span>"
      end
    else

      if (!scenario_name.nil?)
        result << " &gt; <span>#{scenario_name}</span>"
      end
    end
    result
  end
end
