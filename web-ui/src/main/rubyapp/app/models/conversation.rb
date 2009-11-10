class Conversation < ActiveRecord::Base
  belongs_to :system

  has_one :transport, :foreign_key => :inbound_transport
  has_one :transport, :foreign_key => :outbound_transport
  has_one :format, :foreign_key => :inbound_format
  has_one :format, :foreign_key => :outbound_format

  has_many :scenarios

  validates_presence_of :name, :system_id
  # Transports and formats shoulb be set before saving as well
  validates_presence_of :inbound_transport, :outbound_transport
  validates_presence_of :inbound_format, :outbound_format
end
