class System < ActiveRecord::Base
  has_many :conversations ,:dependent => :destroy

  validates_presence_of :name, :script_language
end
