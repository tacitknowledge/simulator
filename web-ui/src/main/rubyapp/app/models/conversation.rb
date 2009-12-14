# == Schema Information
#
# Table name: conversations
#
#  id                    :integer(10)     not null, primary key
#  system_id             :integer(10)     not null
#  name                  :string(255)     not null
#  description           :text
#  enabled               :boolean
#  inbound_transport_id  :integer(10)
#  outbound_transport_id :integer(10)
#  inbound_format_id     :integer(10)
#  outbound_format_id    :integer(10)
#  created_at            :datetime
#  updated_at            :datetime
#  default_response      :string(1000)
#

class Conversation < ActiveRecord::Base
  belongs_to :system


  belongs_to :in_transport,
             :foreign_key => :inbound_transport_id,
             :class_name => "Transport",
             :autosave => true,
             :dependent => :destroy

  belongs_to :out_transport,
             :foreign_key => :outbound_transport_id,
             :class_name => "Transport",
             :autosave => true,
             :dependent => :destroy
  
  belongs_to :in_format,
             :foreign_key => :inbound_format_id,
             :class_name => "Format",
             :autosave => true,
             :dependent => :destroy

  belongs_to :out_format,
             :foreign_key => :outbound_format_id,
             :class_name => "Format",
             :autosave => true,
             :dependent => :destroy
             



#  has_one :transport,  :foreign_key => :inbound_transport_id ,:as => 'trrrr'
#  has_one :transport, :foreign_key => :outbound_transport_id ,:as => 'drrrr'
#  has_one :format, :foreign_key => :inbound_format_id  ,:as => 'zrrrr'
#  has_one :format, :foreign_key =>:outbound_format_id  ,:as => 'srrrr'

  has_many :scenarios ,:dependent => :destroy

  validates_presence_of :name, :system_id
  # Transports and formats should be set before saving as well
  validates_presence_of :in_transport, :out_transport
  validates_presence_of :in_format, :out_format
end
