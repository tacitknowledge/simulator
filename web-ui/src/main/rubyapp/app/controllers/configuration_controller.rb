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
      render :xml => doc.to_s() ,:content_type=>"application/xml"
    rescue Exception => e
      render :template => "error"
    end
  end

  def import
    begin
      file_path = params[:file]
      file = File.new(file_path)
      doc = REXML::Document.new file
      import_xml(doc)
      render :text => {:message => "Successfully imported"} 
    rescue Exception => e
      render :text => {:message => "Error:", :error => e.message}.to_s
    end
  end
end
