class TransportConfiguration < ActiveRecord::Base
  belongs_to :transport,
             :foreign_key => :transport_id, 
             :class_name => 'Transport'

  validates_presence_of :attribute_name, :attribute_value
end
