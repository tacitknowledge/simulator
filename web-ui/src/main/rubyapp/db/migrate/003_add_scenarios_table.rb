class AddScenariosTable < ActiveRecord::Migration
  def self.up
    create_table :scenarios do |t|
      t.integer :conversation_id, :null => false
      t.string :name, :null => false
      t.text :criteria_script  
      t.text :execution_script
      t.boolean :is_active, :default => false

      t.timestamps
    end
  end

  def self.down
    drop_table :scenarios
  end
end
