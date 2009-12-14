# == Schema Information
#
# Table name: formats
#
#  id             :integer(10)     not null, primary key
#  format_type_id :integer(10)     not null
#  created_at     :datetime
#  updated_at     :datetime
#

class Format < ActiveRecord::Base
#  belongs_to :conversation
  belongs_to :format_type

  has_many :configurations,
           :class_name => 'FormatConfiguration',
           :dependent => :destroy,
           :autosave => true

  validates_presence_of :format_type_id
end