class Format < ActiveRecord::Base
#  belongs_to :conversation
  belongs_to :format_type
  
  has_many :format_configurations,
    :dependent => :destroy,
    :autosave => true

  validates_presence_of :format_type_id
end
