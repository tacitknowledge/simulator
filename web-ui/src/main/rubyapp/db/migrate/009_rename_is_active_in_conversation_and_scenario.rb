class RenameIsActiveInConversationAndScenario < ActiveRecord::Migration
  def self.up
    rename_column :conversations,:is_active,:enabled
    rename_column :scenarios,:is_active,:enabled
  end

  def self.down
    rename_column :conversations,:enabled,:is_active
    rename_column :scenarios,:enabled,:is_active
  end
end
