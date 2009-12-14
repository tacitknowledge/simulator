# == Schema Information
#
# Table name: systems
#
#  id              :integer(10)     not null, primary key
#  name            :string(255)     not null
#  description     :text
#  script_language :string(255)     default("JavaScript"), not null
#  created_at      :datetime
#  updated_at      :datetime
#

class System < ActiveRecord::Base
  has_many :conversations ,:dependent => :destroy

  validates_presence_of :name, :script_language
end