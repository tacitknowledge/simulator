class Transport < ActiveRecord::Base
  belongs_to :conversation
  belongs_to :transport_type

  has_many :configurations

  validates_presence_of :transport_type_id, :conversation_id
end
