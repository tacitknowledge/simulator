# == Schema Information
#
# Table name: format_types
#
#  id         :integer(10)     not null, primary key
#  name       :string(255)     not null
#  class_name :string(255)     not null
#  created_at :datetime
#  updated_at :datetime
#

class FormatType < ActiveRecord::Base
#  has_many :formats

  validates_presence_of :name, :class_name

   #Retrieves class name of a format given a format name
  def self.get_format_class_name_by_name(name)
    format_type = FormatType.find(:first, :conditions => ["name = ?", name], :select => "class_name")
    if format_type
      return format_type.class_name
    end
  end

end
