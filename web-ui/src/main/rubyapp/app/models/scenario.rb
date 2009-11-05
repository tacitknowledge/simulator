class Scenario < ActiveRecord::Base
  belongs_to :conversation
  has_one :transport, foreign_key => "inbound_transport"
  has_one :transport, foreign_key => "outbound_transport"
end
