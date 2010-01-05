require "rexml/document"
#Configuration controller is responsible for importing/exporting system configuration from/to xml files
#
class ConfigurationController < ApplicationController

  include ConfigurationHelper

# this magic method fixes file upload problems.
  protect_from_forgery :only => [:create, :update, :destroy]

  def export
    begin
      systems = System.find(:all)

      #doc - REXML document
      doc = systems_to_xml(systems)
      #todo add error handling

#      response.headers["Content-Type"] = 'text/xml'
      render :xml => doc.to_s(2) ,:content_type=>"text/json"
    rescue Exception => e
      render :template => "error"
    end
  end

  def import
    begin

      file_path = params[:file].local_path

      file = File.new(file_path)
      doc = REXML::Document.new file
      import_xml(doc)
      render :text => {:message => "Successfully imported"}.to_s 
    rescue Exception => e
      render :text => {:message => "Error" }.to_s
    end
  end
end
