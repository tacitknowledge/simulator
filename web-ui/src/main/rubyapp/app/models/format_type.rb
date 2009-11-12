class FormatType < ActiveRecord::Base
#  has_many :formats

  validates_presence_of :name, :class_name
end
