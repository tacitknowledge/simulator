class Configuration < ActiveRecord::Base
  belongs_to :transport
  belongs_to :format

  validates_presence_of :attribute_name, :attribute_value
end
