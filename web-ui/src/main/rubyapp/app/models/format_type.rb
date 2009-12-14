# == Schema Information
#
# Table name: format_types
#
#  id         :integer(10)     not null, primary key
#  name       :string(255)     not null
#  class_name :string(255)     not null
#  created_at :datetime
#  updated_at :datetime
#

class FormatType < ActiveRecord::Base
#  has_many :formats

  validates_presence_of :name, :class_name
end
