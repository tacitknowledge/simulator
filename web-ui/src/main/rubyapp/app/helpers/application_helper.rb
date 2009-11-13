# Methods added to this helper will be available to all templates in the application.
module ApplicationHelper
  #Method used in views to build the path to the ext js files
  #based on the configuration set in the environment variables.
  def relative_url_root
    ENV["relative_url_root"]
  end
end
