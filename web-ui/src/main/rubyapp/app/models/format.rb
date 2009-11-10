class Format < ActiveRecord::Base
  belongs_to :conversation
  has_one :format_type
  has_many :configurations
end
