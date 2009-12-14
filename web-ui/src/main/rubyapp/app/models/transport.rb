# == Schema Information
#
# Table name: transports
#
#  id                :integer(10)     not null, primary key
#  transport_type_id :string(255)     not null
#  created_at        :datetime
#  updated_at        :datetime
#

class Transport < ActiveRecord::Base
#  has_one :conversation
  belongs_to :transport_type , :foreign_key => :transport_type_id

  has_many :configurations,
           :class_name => 'TransportConfiguration',
           :dependent => :destroy,
           :autosave => true

  validates_presence_of :transport_type
end