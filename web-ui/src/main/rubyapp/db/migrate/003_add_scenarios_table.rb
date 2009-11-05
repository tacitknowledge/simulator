class AddScenariosTable < ActiveRecord::Migration
  def self.up
    create_table :scenarios do |t|
      t.string :conversation_id, :null => false
      t.string :name, :null => false
      t.text :criteria_script, :null => false
      t.text :execution_script, :null => false
      t.boolean :is_active, :default => false
    end
  end

  def self.down
    drop_table :scenarios
  end
end
