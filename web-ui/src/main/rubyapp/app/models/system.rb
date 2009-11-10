class System < ActiveRecord::Base
  has_many :conversations

  validates_presence_of :name, :script_language
end
