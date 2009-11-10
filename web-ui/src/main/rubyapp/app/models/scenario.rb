class Scenario < ActiveRecord::Base
  belongs_to :conversation

  validates_presence_of :name, :criteria_script, :execution_script
end
