# == Schema Information
#
# Table name: scenarios
#
#  id               :integer(10)     not null, primary key
#  conversation_id  :integer(10)     not null
#  name             :string(255)     not null
#  criteria_script  :text
#  execution_script :text
#  enabled          :boolean
#  created_at       :datetime
#  updated_at       :datetime
#  label            :string(255)
#

class Scenario < ActiveRecord::Base
  belongs_to :conversation

  validates_presence_of :name, :criteria_script, :execution_script
end
