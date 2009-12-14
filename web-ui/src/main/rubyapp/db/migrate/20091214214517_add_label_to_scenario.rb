class AddLabelToScenario < ActiveRecord::Migration
  def self.up
    add_column :scenarios, :label, :string
  end

  def self.down
    remove_column :scenarios, :label
  end
end
