class Conversation < ActiveRecord::Base
  has_many :scenario
  belongs_to :system , :foreign_key => :system_id
  has_one :transport, :foreign_key => :transport_type_id, :class_name => 'Transport'
  has_one :transport, :foreign_key => :transport_type_id,  :class_name => 'Transport'
end
