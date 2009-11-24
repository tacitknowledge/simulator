class Format < ActiveRecord::Base
#  belongs_to :conversation
  belongs_to :format_type

  has_many :configurations,
           :class_name => 'FormatConfiguration',
           :dependent => :destroy,
           :autosave => true

  validates_presence_of :format_type_id
end
