class Transport < ActiveRecord::Base
#  has_one :conversation
  belongs_to :transport_type , :foreign_key => :transport_type_id

  has_many :configurations,
           :class_name => 'TransportConfiguration',
           :dependent => :destroy,
           :autosave => true

  validates_presence_of :transport_type
end
